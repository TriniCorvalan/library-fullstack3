# ---- Etapa 1: compilar con Maven Wrapper ----
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
# mvnw puede venir con saltos de linea de Windows (CRLF)
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -q -B dependency:go-offline
COPY src src
RUN ./mvnw -q -B clean package -DskipTests

# ---- Etapa 2: imagen liviana solo con el JRE ----
FROM eclipse-temurin:25-jre

WORKDIR /app
# Sin apt-get: evita fallos de DNS en Docker Desktop (ports.ubuntu.com).
# jar (JDK) sirve para listar/extraer el ZIP del wallet en el entrypoint.
RUN useradd --system --no-create-home appuser \
 && mkdir -p /app/wallet \
 && chown appuser:appuser /app/wallet
COPY --from=build /opt/java/openjdk/bin/jar /usr/local/bin/jar
COPY --from=build /app/target/library-0.0.1-SNAPSHOT.jar /app/library.jar
COPY docker-entrypoint.sh /app/docker-entrypoint.sh

ENV TNS_ADMIN=/app/wallet
RUN sed -i 's/\r$//' /app/docker-entrypoint.sh \
 && chmod 0555 /app/docker-entrypoint.sh
USER appuser
EXPOSE 8080

ENTRYPOINT ["/app/docker-entrypoint.sh"]
