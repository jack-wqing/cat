<%@ page session="false" language="java" pageEncoding="UTF-8" %>
<h2 style="color: #1373e2">天眼查CAT</h2>
</br>
<h4 class="text-success">CAT使用文档</h4>
<h5>
	如何配置CAT告警的详细过程：<a target="_blank" href="http://wiki.jindidata.com/pages/viewpage.action?pageId=31721223">学会配置告警规则</a>
</h5>
<h5>我们读CAT底层源码的时候也做了一些总结：<a target="_blank" href="http://wiki.jindidata.com/pages/viewpage.action?pageId=30292192">总结</a></h5>
</br>
<h4 class="text-success">问题反馈</h4>
<h5>
	CAT紧急问题，飞书私信【杨航】、【李茂展】
</h5>
<h5>
	其他疑问可以这样反馈；<a target="_blank" href="http://wiki.jindidata.com/pages/viewpage.action?pageId=31721386">点我去反馈疑问</a>
</h5>
</br>
<h4 class="text-success">CAT监控大盘</h4>
<div>
	<a id="navmetricDashboard" class="btn btn-sm btn-primary" target="_blank" href="/cat/r/top?op=view&domain=${model.domain}">系统报错大盘</a>
<%--	<a id="navdashboard" class="btn btn-sm btn-primary" target="_blank" href="/cat/r/dependency?op=dashboard&domain=${model.domain}">应用监控大盘</a>--%>
<%--	<a id="navbussiness" class="btn btn-sm btn-primary" target="_blank" href="/cat/r/metric?op=dashboard&domain=${model.domain}">业务监控大盘</a>--%>
	<%--	<a id="navnet" class="btn btn-sm btn-primary" target="_blank" href="/cat/r/network?op=dashboard&domain=${model.domain}">网络监控大盘</a>--%>
	<%--	<a id="navbussiness" class="btn btn-sm btn-primary" target="_blank" href="/cat/r/storage?op=dashboard&domain=${model.domain}">数据库监控大盘</a>--%>
</div>
</br>
<h4 class="text-success">CAT其他环境</h4>
<div>
	<a class="btn btn-sm btn-primary" href="http://cat.yufa.ty.ink">预发环境</a>
	<a class="btn btn-sm btn-primary" href="http://172.24.116.129:8080/cat/r">测试环境</a>
	<%--<a class="btn btn-sm btn-primary" href="http://ppe.cat.dp/cat/r/">PPE环境</a>--%>
	<%--<a class="btn btn-sm btn-primary" href="http://cat.dianpingoa.com/cat/r/">生产环境</a>--%>
</div>
</br>

<h4 class="text-success">接入公司</h4>
<table>
	<tr>
		<td><a target="_blank" href="http://www.tianyancha.com/"><img  class="img-polaroid" width="124" height="45"  src="${model.webapp}/images/logo/tianyancha.png"/></a></td>
		<td><a target="_blank" href="https://www.lufax.com/"><img  class="img-polaroid"  src="${model.webapp}/images/logo/lufax.png"/></a></td>
		<td><a target="_blank" href="http://www.ly.com/"><img  class="img-polaroid"  src="${model.webapp}/images/logo/ly.png"/></a></td>
	</tr>
	<td></td>
	</tr>
</table>