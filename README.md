# Notes

 * **WARNING**: This service uses logs to save data. Restarting the service too much times (around 1000 times) may cause to much storage usage.
 * **WARNING**: Please specify a path to the file where DB data should be stored in `/org/gush/Server.java`. The service will create this file for you if it doesn't exist.
 * On my M1 CPU the service can handle 400k queries/sec.
 * I'm not sure about thread safety because I'm not familiar with architecture of Vert.x framework. Code currently assumes single-threaded execution.
