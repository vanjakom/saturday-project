# saturday-project

## goal
Be able to easily distribute ad hoc code from development box to compute one. Short lived  glue code made by composing library functions code should be good candidate for this. Both Java ( minimalistic code ) and Clojure code should be deployed.

## prerequisites
clojure with modified source code to be built and target/classes added as repository. following modifications are done to source code:

diff --git a/src/jvm/clojure/lang/RT.java b/src/jvm/clojure/lang/RT.java
index f4bb9a5b..860d4f80 100644
--- a/src/jvm/clojure/lang/RT.java
+++ b/src/jvm/clojure/lang/RT.java
@@ -2169,10 +2169,12 @@ static public ClassLoader makeClassLoader(){
 }
 
 static public ClassLoader baseLoader(){
+       /*
        if(Compiler.LOADER.isBound())
                return (ClassLoader) Compiler.LOADER.deref();
        else if(booleanCast(USE_CONTEXT_CLASSLOADER.deref()))
                return Thread.currentThread().getContextClassLoader();
+       */
        return Compiler.class.getClassLoader();
 }

## componenets
bootstrap - core of project used for running code by loading classes and clojure code from alternative sources in minimalistic jvm ( empty classpath )
utils - utilities used for preparation of repositories

## bootstraps
( cd bootstrap && mvn clean package )
java -cp target/bootstrap-1.0.0-SNAPSHOT.jar com.mungolab.dynamic.bootstrap.CljScraperStart
java -jar target/bootstrap-1.0.0-SNAPSHOT.jar com.mungolab.dynamic.sampleapp.Test1
java -cp target/bootstrap-1.0.0-SNAPSHOT.jar com.mungolab.dynamic.bootstrap.Static com.mungolab.dynamic.sampleapp.Test2 start
java -cp target/bootstrap-1.0.0-SNAPSHOT.jar com.mungolab.dynamic.bootstrap.Static "test\$fn1" "invokeStatic"
java -cp target/bootstrap-1.0.0-SNAPSHOT.jar com.mungolab.dynamic.bootstrap.Clojure "mytest/fn1"