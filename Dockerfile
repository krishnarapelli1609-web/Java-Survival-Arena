
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY src ./src

RUN mkdir -p out && javac --add-modules jdk.httpserver -d out src/main/java/org/example/*.java

EXPOSE 10000

CMD ["sh", "-c", "java --add-modules jdk.httpserver -cp out org.example.ArenaServer"]
