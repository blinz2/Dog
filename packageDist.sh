mvn clean package
mkdir dist
mv target/Dog*.jar dist/Dog.jar
mvn javadoc:javadoc
mv target/site/apidocs dist/javadoc
cp -r src/main/java dist/src
rm -r target
