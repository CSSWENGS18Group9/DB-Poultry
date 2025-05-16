# Docker

## Dependencies

We will be using Docker to contain all the necessary depencies for the project:
- [Adoptium (jdk-21.0.7+6)](https://adoptium.net/)
- [Kotlin 21.20](https://github.com/JetBrains/kotlin/releases/tag/v2.1.20)
- [Gradle 8.14](https://gradle.org/)
- MySQL

## Using Docker

> To Do

## Finishing the Project

Once the project is done, create the Docker [image](https://hub.docker.com/_/docker). This will contain the entire codebase and the dependencies mentioned above.

As a standard, our Docker image will be names as follows: `DBPoultry-<Version Number>`. 

To run, simply:
```bash
$ docker run DBPoultry-<Version Number>
```

> In the future, we may want an executable file (`.exe`) written in a System language, to automatically run the command listed above. To make it easier for the client to run the Docker image.
