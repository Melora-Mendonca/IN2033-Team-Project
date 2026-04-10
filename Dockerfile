FROM eclipse-temurin:23-jre-noble

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends \
        xvfb \
        x11vnc \
        novnc \
        websockify \
        x11-xserver-utils \
        libxext6 \
        libxrender1 \
        libxtst6 \
        libxi6 \
        fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

COPY IN2033-Team-Project.jar app.jar
COPY start.sh .
COPY data/ ./data/
RUN chmod +x start.sh

EXPOSE 5900 6082 8081

ENTRYPOINT ["./start.sh"]