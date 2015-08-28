EEA Template Refresh Module
===========================

This is a module to embed the EEA website style into Tomcat applications. It
is assumed that the embedding application has the following lines in web.xml.

```
<servlet>
    <servlet-name>RefreshTemplateServlet</servlet-name>
    <servlet-class>eionet.eea.template.RefreshTemplateServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>RefreshTemplateServlet</servlet-name>
    <url-pattern>/refreshtemplate</url-pattern>
</servlet-mapping>
```
