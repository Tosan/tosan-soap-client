# tosan-soap-client

This project provides a java library that facilitate common requirements of a soap webservice client like
ssl, proxy and timeout setting and logging request/response with ability to mask sensitive data.

## Requirement
We have some plans to upgrade our projects to spring boot 3,
so we have to upgrade the projects dependencies and migrate javax namespaces to jakarta.

So the main branch is working with jakarta namespaces and needs java 17 and later. 

If you still want to use this library with java 8 and javax namespace please use branch 'java_8' 
and the 1.x.x releases. 