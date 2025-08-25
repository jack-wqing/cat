/*
 * Copyright (c) 2011-2018, Meituan Dianping. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unidal.cat.message.storage.local;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.buffer.ByteBuf;
import org.unidal.cat.message.storage.Bucket;
import org.unidal.cat.message.storage.FileType;
import org.unidal.cat.message.storage.PathBuilder;
import org.unidal.cat.message.storage.internals.ByteBufCache;
import org.unidal.cat.message.storage.internals.DefaultBlock;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.Cat;
import com.dianping.cat.config.server.ServerConfigManager;
import com.dianping.cat.message.internal.MessageId;

// 本地数据存储: 最小存储单元Buncket
@Named(type = Bucket.class, value = "local", instantiationStrategy = Named.PER_LOOKUP)
public class LocalBucket implements Bucket {

	@Inject("local")
	private PathBuilder m_builder;

	@Inject
	private ByteBufCache m_bufCache;

	@Inject
	private ServerConfigManager m_config;

	private DataHelper m_data = new DataHelper();

	private IndexHelper m_index = new IndexHelper();

	private boolean m_nioEnabled = true;

	private boolean m_writeMode;

	private AtomicInteger m_count = new AtomicInteger();

	@Override
	public synchronized void close() {
		if (m_index.isOpen()) {
			m_data.close();

			if (m_writeMode) {
				m_index.flushAndClose();
			} else {
				m_index.close();
			}
		}
	}

	@Override
	public void flush() {
		try {
			m_data.m_out.flush();
		} catch (Exception e) {
			Cat.logError(e);
		}
	}
	// 存储数据是以Block压缩的方式进行的
	@Override
	public ByteBuf get(MessageId id) throws IOException {
		long address = m_index.read(id);

		if (address <= 0) {
			return null;
		} else {
			int segmentOffset = (int) (address & 0xFFFFFFL);
			long dataOffset = address >> 24;
			// 每次数据消息读取会读取整个Block
			byte[] data = m_data.read(dataOffset);

			if (data != null) {
				DefaultBlock block = new DefaultBlock(id, segmentOffset, data);
				// 解压数据
				return block.unpack(id);
			} else {
				return null;
			}
		}
	}

	@Override
	public boolean initialize(String domain, String ip, int hour, boolean writeMode) throws IOException {
		m_nioEnabled = m_config.getStroargeNioEnable();
		long timestamp = hour * 3600 * 1000L;
		Date startTime = new Date(timestamp);
		File indexPath = new File(m_builder.getPath(domain, startTime, ip, FileType.INDEX));
		File dataPath = new File(m_builder.getPath(domain, startTime, ip, FileType.DATA));

		m_writeMode = writeMode;
		m_index.init(indexPath);
		m_data.init(dataPath);
		return true;
	}

	@Override
	public synchronized void puts(ByteBuf data, Map<MessageId, Integer> mappings) throws IOException {
		long dataOffset = m_data.getDataOffset();

		m_data.write(data);

		for (Map.Entry<MessageId, Integer> e : mappings.entrySet()) {
			MessageId id = e.getKey();
			int offset = e.getValue();

			m_index.write(id, dataOffset, offset);
		}
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", getClass().getSimpleName(), m_data.getPath());
	}

	// 数据文件存储
	private class DataHelper {
		private File m_path;

		private RandomAccessFile m_file;

		private long m_offset;

		// 是一个FilterOutputStream,允许Java以机器无关的方式把Java基本类型写入到文件
		private DataOutputStream m_out;

		private void close() {
			try {
				if (m_out != null) {
					m_out.close();
				}
			} catch (IOException e) {
				Cat.logError(e);
			}

			try {
				m_file.close();
			} catch (IOException e) {
				Cat.logError(e);
			}

			m_file = null;
		}

		private long getDataOffset() {
			return m_offset;
		}

		private File getPath() {
			return m_path;
		}

		private void init(File dataPath) throws IOException {
			m_path = dataPath;
			m_path.getParentFile().mkdirs();

			m_file = new RandomAccessFile(m_path, "rw"); // read-write
			m_offset = m_path.length();
			m_out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(m_path, true), (int) SEGMENT_SIZE));

			if (m_offset == 0) {
				m_out.writeInt(-1);
				m_offset += 4;
			}
		}
		// 数据文件每个消息的存储内容是: 消息长度[INT] + 消息内容
		private byte[] read(long dataOffset) throws IOException {
			if (dataOffset < m_offset) {
				m_file.seek(dataOffset);

				int len = m_file.readInt();

				if (len > 0) {
					byte[] data = new byte[len];

					m_file.readFully(data);

					return data;
				}
			}
			return null;
		}

		private void write(ByteBuf data) throws IOException {
			int len = data.readableBytes();

			m_out.writeInt(len);
			data.readBytes(m_out, len);
			m_offset += len + 4;
		}
	}

	// 索引文件操作帮助类
	private class IndexHelper {
		// 随机访问类
		private RandomAccessFile m_file;
		// 索引文件Path
		private File m_path;
		// FileChannel
		private FileChannel m_indexChannel;
		// 一级索引
		private Header m_header = new Header();
		// ip缓存: 每个ip缓存最多两个segmetn
		private Map<String, SegmentCache> m_caches = new LinkedHashMap<String, SegmentCache>();

		private void close() {
			try {
				m_indexChannel.close();
				m_file.close();
			} catch (IOException e) {
				Cat.logError(e);
			}
			m_file = null;
			m_caches.clear();
		}

		private void flushAndClose() {
			try {
				// 一级索引刷新
				m_header.m_segment.flushAndClose();
				// 二级索引刷新
				for (SegmentCache cache : m_caches.values()) {
					cache.flushAndClose();
				}
			} catch (IOException e) {
				Cat.logError(e);
			}

			if (m_nioEnabled) {
				try {
					m_indexChannel.force(false);
					m_indexChannel.close();
				} catch (IOException e) {
					Cat.logError(e);
				}
			}

			try {
				m_file.close();
			} catch (IOException e) {
				Cat.logError(e);
			}

			m_file = null;
			m_caches.clear();
		}

		private Segment getSegment(String ip, long id) throws IOException {
			SegmentCache cache = m_caches.get(ip);

			if (cache == null) {
				cache = new SegmentCache();
				m_caches.put(ip, cache);
			}

			return cache.findOrCreateNextSegment(id);
		}
		// 从索引文件初始化操作
		private void init(File indexPath) throws IOException {
			m_path = indexPath;
			m_path.getParentFile().mkdirs();

			// read-write without meta sync
			m_file = new RandomAccessFile(m_path, "rw");
			m_indexChannel = m_file.getChannel();

			long size = m_file.length();
			// 总的一级索引的数量；文件大小：每个索引单元为4096 * 32KB；就是总共有多少个 Header表
			int totalHeaders = (int) Math.ceil((size * 1.0 / (((long) ENTRY_PER_SEGMENT) * SEGMENT_SIZE)));
			// 默认为1
			if (totalHeaders == 0) {
				totalHeaders = 1;
			}
			// 初始化所有的最小索引单元的头部
			// 定位到最新的Header Segment
			for (int i = 0; i < totalHeaders; i++) {
				m_header.load(i);
			}
		}

		private boolean isOpen() {
			return m_file != null;
		}
		// 数据文件的位置 40位存储块地址， 24位偏移地址
		private long read(MessageId id) throws IOException {
			int index = id.getIndex();
			long position = m_header.getOffset(id.getIpAddressValue(), index, false);

			int segmentId = (int) (position / SEGMENT_SIZE);
			int offset = (int) (position % SEGMENT_SIZE);
			Segment segment = getSegment(id.getIpAddressInHex(), segmentId);

			if (segment != null) {
				try {
					return segment.readLong(offset);
				} catch (EOFException e) {
					// ignore it
				}
			} else if (position > 0) {
				m_file.seek(position);

				return m_file.readLong();
			}

			return -1;
		}
		// 写入块地址 和 块偏移地址: 通过索引文件ip + baseindex + 8byte推到出
		private void write(MessageId id, long blockAddress, int blockOffset) throws IOException {
			long position = m_header.getOffset(id.getIpAddressValue(), id.getIndex(), true);
			long address = position / SEGMENT_SIZE;
			int offset = (int) (position % SEGMENT_SIZE);
			Segment segment = getSegment(id.getIpAddressInHex(), address);
			long value = (blockAddress << 24) + blockOffset;

			if (segment != null) {
				segment.writeLong(offset, value);
			} else {
				if (m_count.incrementAndGet() % 1000 == 0) {
					Cat.logEvent("AbnormalBlock", id.getDomain());
				}
				// NIO
				if (m_nioEnabled) {
					m_indexChannel.position(position);

					ByteBuffer buf = ByteBuffer.allocate(8);
					buf.putLong(value);
					buf.flip();
					m_indexChannel.write(buf);
				} else {
					m_file.seek(position);
					m_file.writeLong(value);
				}
			}
		}
		// 每一个最小索引单位中Header一级索引管理二级索引的方式
		private class Header {
			// 映射表: IP, baseIndex, segementIndex
			// 初始化会加载所有的以及索引
			private Map<Integer, Map<Integer, Integer>> m_table = new LinkedHashMap<Integer, Map<Integer, Integer>>();

			private int m_nextSegment;

			private Segment m_segment;

			private int m_offset;

			// 通过Ip和Index 二级索引位于那个段

			private Integer findSegment(int ip, int index, boolean createIfNotExists) throws IOException {
				Map<Integer, Integer> map = m_table.get(ip);

				if (map == null && createIfNotExists) {
					map = new HashMap<Integer, Integer>();
					m_table.put(ip, map);
				}

				Integer segmentId = map == null ? null : map.get(index);

				if (segmentId == null && createIfNotExists) {
					long value = (((long) ip) << 32) + index;

					segmentId = m_nextSegment;
					map.put(index, segmentId);

					m_segment.writeLong(m_offset, value);
					m_offset += 8;

					m_nextSegment++;

					if (m_nextSegment % (ENTRY_PER_SEGMENT) == 0) {
						// last segment is full, create new one
						m_segment.flushAndClose();
						m_segment = new Segment(m_indexChannel, ((long) m_nextSegment) * SEGMENT_SIZE);

						m_nextSegment++; // skip self head data
						m_segment.writeLong(0, -1); // write magic code
						m_offset = 8;
					}
				}

				return segmentId;
			}

			private long getOffset(int ip, int seq, boolean createIfNotExists) throws IOException {
				int segmentIndex = seq / MESSAGE_PER_SEGMENT;
				int segmentOffset = (seq % MESSAGE_PER_SEGMENT) * BYTE_PER_MESSAGE;
				Integer segmentId = findSegment(ip, segmentIndex, createIfNotExists);

				if (segmentId != null) {
					return ((long) segmentId) * SEGMENT_SIZE + segmentOffset;
				} else {
					return -1;
				}
			}
			// headBlockIndex从0开始
			private void load(int headBlockIndex) throws IOException {
				Segment segment = new Segment(m_indexChannel, ((long) headBlockIndex) * ENTRY_PER_SEGMENT * SEGMENT_SIZE);
				long magicCode = segment.readLong();

				if (magicCode == 0) {
					segment.writeLong(0, -1);
				} else if (magicCode != -1) {
					throw new IOException("Invalid index file: " + m_path);
				}

				m_segment = segment;
				m_nextSegment = 1 + ENTRY_PER_SEGMENT * headBlockIndex;
				m_offset = 8;

				int readerIndex = 1;

				while (readerIndex < ENTRY_PER_SEGMENT) {
					int ip = segment.readInt();
					int index = segment.readInt();

					readerIndex++;

					if (ip != 0) {
						Map<Integer, Integer> map = m_table.get(ip);

						if (map == null) {
							map = new HashMap<Integer, Integer>();
							m_table.put(ip, map);
						}

						Integer segmentNo = map.get(index);

						if (segmentNo == null) {
							segmentNo = m_nextSegment++;

							map.put(index, segmentNo);
						}

						m_offset += 8;
					} else {
						break;
					}
				}
			}
		}

		// 每个段都是32kb数据 + BufCache 8000个32kb
		private class Segment {
			private FileChannel m_segmentChannel;

			private long m_address;

			private ByteBuffer m_buf;

			private Segment(FileChannel channel, long address) throws IOException {
				m_segmentChannel = channel;
				m_address = address;

				m_buf = m_bufCache.get();
				// m_buf = ByteBuffer.allocate(SEGMENT_SIZE);
				m_buf.mark();
				m_segmentChannel.read(m_buf, address);
				m_buf.reset();
			}

			private synchronized void flushAndClose() throws IOException {
				if (m_buf != null) {
					int pos = m_buf.position();

					m_buf.position(0);
					m_segmentChannel.write(m_buf, m_address);
					m_buf.position(pos);
					m_bufCache.put(m_buf);
					m_buf = null;
				} else {
					Cat.logEvent("CloseBucket", "Duplicate:" + m_path.getAbsolutePath());
				}
			}

			private int readInt() throws IOException {
				return m_buf.getInt();
			}

			private long readLong() throws IOException {
				return m_buf.getLong();
			}

			private long readLong(int offset) throws IOException {
				return m_buf.getLong(offset);
			}

			@Override
			public String toString() {
				return String.format("%s[address=%s]", getClass().getSimpleName(), m_address);
			}

			private void writeLong(int offset, long value) throws IOException {
				m_buf.putLong(offset, value);
			}
		}
		// 段缓存
		private class SegmentCache {
			private final static int CACHE_SIZE = 2;

			private long m_maxSegmentId;

			private Map<Long, Segment> m_latestSegments = new LinkedHashMap<Long, Segment>();

			private synchronized void flushAndClose() throws IOException {
				for (Segment segment : m_latestSegments.values()) {
					segment.flushAndClose();
				}
				m_latestSegments.clear();
			}
			// 缓存两个段
			private Segment findOrCreateNextSegment(long segmentId) throws IOException {
				Segment segment = m_latestSegments.get(segmentId);

				if (segment == null) {
					if (segmentId > m_maxSegmentId) {
						if (m_latestSegments.size() >= CACHE_SIZE) {
							removeOldSegment();
						}

						segment = new Segment(m_indexChannel, segmentId * SEGMENT_SIZE);

						m_latestSegments.put(segmentId, segment);
						m_maxSegmentId = segmentId;
					}
				}

				return segment;
			}

			private void removeOldSegment() throws IOException {
				Entry<Long, Segment> first = m_latestSegments.entrySet().iterator().next();
				Segment segment = m_latestSegments.remove(first.getKey());

				segment.flushAndClose();
			}
		}
	}

}
