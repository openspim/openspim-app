# ImageJ2 adapter for OpenSPIM

ImageJ2 offers an easy way to configure many aspects of its behavior: <a href="http://scijava.org/">SciJava plugins</a>.

One such behavior to configure allows to change the application name and icon, another behavior allows to run some code before startup. We use the former to adapt ImageJ2/Fiji to serve as the OpenSPIM application and we implement the latter to redirect stdout/stderr as well as ensure that the `Micro-Manager-dev` *and* `OpenSPIM` update sites are active, in the correct order.
