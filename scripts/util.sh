# fn:  readmime - return mime type for supported extension
# in:  supports STDIN or $1
# out: writes mime type to STDOUT
function readmime() {
    if read -t 0; then
        read IN
    else
        IN="$1"
    fi
    if [ -z "$IN" ]; then
        return 1
    fi
        
    DEFAULT="text/plain"
    declare -A EXT_MIME=(["html"]="text/html" \
                         ["jar"]="application/java-archive" \
                         ["png"]="image/png" \
                         ["txt"]="text/plain" \
                         ["jnlp"]="application/x-java-jnlp-file")
    EXT=$(echo $IN | sed 's#.*\.\([a-z]\+\)#\1#g')
    if [ -n "${EXT_MIME[$EXT]}" ]; then
        echo "${EXT_MIME[$EXT]}"
    else
        echo "$DEFAULT"
    fi
}
