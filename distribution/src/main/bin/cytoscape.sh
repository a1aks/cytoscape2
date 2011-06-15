#!/bin/sh
#
# Run cytoscape from a jar file
# This script is a UNIX-only (i.e. Linux, Mac OS, etc.) version
#-------------------------------------------------------------------------------

script_path="$(dirname -- $(readlink -f $0))"

#vm_options_path=$HOME/.cytoscape
vm_options_path=$script_path

if [ -r $vm_options_path/Cytoscape.vmoptions ]; then
    java `cat "$vm_options_path/Cytoscape.vmoptions"` -jar "$script_path/cytoscape.jar" -p "$script_path/plugins" "$@"
else # Just use sensible defaults.
    echo '*** Missing Cytoscape.vmoptions, falling back to using defaults!'
    java -Dswing.aatext=true -Dawt.useSystemAAFontSettings=lcd -Xss10M -Xmx1550M \
	-jar "$script_path/cytoscape.jar" -p "$script_path/plugins" "$@"
fi

