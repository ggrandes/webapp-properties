# webapp-properties

Load Properties from a file into SystemProperties or as an Attribute in ServletContext, using WebappBaseName as reference in a servlet container like Tomcat. Open Source Java project under Apache License v2.0

### Current Stable Version is [1.0.0](https://search.maven.org/#search|ga|1|g%3Aorg.javastack%20a%3Awebapp-properties)

---

## DOC

#### Installation:

* Place `webapp-properties-x.x.x.jar` in `tomcat/lib/`

#### Configuration:

```xml
<!-- Context Listener for Servlet Container -->
<!-- tomcat/conf/web.xml or WEB-INF/web.xml -->
<listener>
	<description>Load Properties from a file into SystemProperties or as an Attribute in ServletContext, using WebappBaseName as reference</description>
	<listener-class>org.javastack.webappproperties.WebAppPropertiesContextListener</listener-class>
</listener>
```

###### Note: If you have more than one webapp per JVM and you are loading this to system-properties without [SystemClassLoaderProperties](https://github.com/ggrandes/systemclassloaderproperties) the keys with same name can collide and view unexpected values. Properties as Attribute in ServletContext may be better alternative than Global System Properties.  

#### Naming conventions and order of load:

1. Default system properties (never overwrite existing property): `{baseDir}/appconf/{webappBaseName}/system-properties-default.properties`
2. Forced system properties (always overwrite existing property): `{baseDir}/appconf/{webappBaseName}/system-properties-forced.properties`
3. Context Attribute properties: `{baseDir}/appconf/{webappBaseName}/context.properties`

###### `{webappBaseName}` follow the [Tomcat Basenames](https://tomcat.apache.org/tomcat-7.0-doc/config/context.html#Naming) convention

#### Resolution order of `{baseDir}` (system property):

1. org.javastack.webappproperties.conf
2. catalina.base
3. catalina.home
4. user.dir

###### pick next system property if key is not defined.

#### Usage of Context Attribute properties:

```java
final String CONST_CONTEXT_ATTRIBUTE_NAME = "org.javastack.webappproperties.ctx";
final Properties p = (Properties) getServletContext().getAttribute(CONST_CONTEXT_ATTRIBUTE_NAME);
```

---

## MAVEN

    <dependency>
        <groupId>org.javastack</groupId>
        <artifactId>webapp-properties</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>

---
Inspired in [SystemClassLoaderProperties](https://github.com/ggrandes/systemclassloaderproperties/) and [CatalinaProperties](https://github.com/apache/tomcat70/blob/trunk/java/org/apache/catalina/startup/CatalinaProperties.java), this code is Java-minimalistic version.
