export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.5
export PATH=$JAVA_HOME/bin:$PATH
mvn install -Dmaven.javadoc.skip=true -Dgpg.skip=true