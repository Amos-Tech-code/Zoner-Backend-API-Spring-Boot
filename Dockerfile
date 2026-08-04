# -----------------------------
# Stage 1 - Build
# -----------------------------
FROM gradle:8.14.3-jdk21 AS builder

WORKDIR /app

# 1. Copy ONLY files needed to resolve dependencies (for caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# 2. Pre-download dependencies (This layer stays cached unless dependencies change)
RUN gradle dependencies --no-daemon

# 3. Copy the rest of your app's source code
COPY src src

# 4. Explicitly build ONLY the executable bootJar (limits memory for Render stability)
RUN gradle bootJar --no-daemon --max-workers=2 -Dorg.gradle.jvmargs="-Xmx512m"

# -----------------------------
# Stage 2 - Runtime
# -----------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root system user for secure production execution
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# FIX: Explicitly target your known jar file name to avoid wildcard overlap
COPY --from=builder /app/build/libs/Zoner-0.0.1-SNAPSHOT.jar Zoner.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","Zoner.jar"]
