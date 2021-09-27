# Set the default LANG to something sensible if it is unset
if [ -z "$LANG" ]; then
    source /etc/locale.conf
    export LANG
fi
