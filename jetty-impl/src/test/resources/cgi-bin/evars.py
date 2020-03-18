#!/usr/bin/python

import os
from cgi import escape

print
"Content-type: text/html"
print
print
"<!doctype html>"
print
"<html>"
print
"<body>"
print
"<pre>"
for k in sorted(os.environ):
    print
    "<b>%s: </b>%s" % (escape(k), escape(os.environ[k]))
print
"</pre>"
print
"</body>"
print
"</html>"
