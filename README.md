# Notes

 * **WARNING**: This service uses logs to save data. Restarting the service too much times may cause to much storage usage.
 * On my M1 CPU the service can handle 400k queries/sec.
 * I'm not sure about thread safety because I'm not familiar with architecture of Vert.x framework. Code currently assumes single-threaded execution.
