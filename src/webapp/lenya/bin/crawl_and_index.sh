#!/bin/sh

DIRNAME=`dirname $0`
echo "INFO: dirname = $DIRNAME"

echo "INFO: HOME = $HOME"
WEBAPP_DIR=$HOME/src/cocoon-lenya/build/lenya/webapp
LIB_DIR=$WEBAPP_DIR/WEB-INF/lib
JAVA=/usr/lib/j2sdk1.4/bin/java
#symlink the xpdf directory to the version you have
XPDF=$HOME/bin/xpdf/pdftotext

CLASSPATH=$WEBAPP_DIR/WEB-INF/classes:$LIB_DIR/log4j-1.2.7.jar:$LIB_DIR/xercesImpl-2.5.0.jar:$LIB_DIR/xml-apis.jar:$LIB_DIR/excalibur-io-1.1.jar

echo "INFO: classpath = $CLASSPATH"


case "$1" in
    index)
        echo ""
        echo "=========================================================="
        echo "Target: $1"
        echo "=========================================================="
        echo ""
        CLASSPATH=$CLASSPATH:$LIB_DIR/lucene-1.3.jar
        echo "INFO: classpath = $CLASSPATH"

        LUCENE_CONF=$2

        DEBUG=true

        echo "INFO: lucene.xconf = $LUCENE_CONF"
        $JAVA -cp $CLASSPATH org.apache.lenya.lucene.IndexConfiguration $LUCENE_CONF
        $JAVA -cp $CLASSPATH org.apache.lenya.lucene.index.Index $LUCENE_CONF $DEBUG

        ###$JAVA -cp $CLASSPATH org.apache.lenya.lucene.IndexHTML $LUCENE_CONF
	;;
    xpdf)
        echo ""
        echo "=========================================================="
        echo "Target: $1"
        echo "=========================================================="
        echo ""

        HTDOCS_DUMP_DIR=$2

        echo "INFO: HTDOCS_DUMP_DIR = $HTDOCS_DUMP_DIR"
        if [ -f $XPDF ]; then
            find $HTDOCS_DUMP_DIR -name "*.pdf" -print -exec $XPDF -htmlmeta {} {}.txt \;
        else
            echo "WARNING: Xpdf not installed: $XPDF"
        fi
	;;
    *)
        echo "Usage: $0 {index|xpdf}"
        exit 1
        ;;
esac

exit 0
