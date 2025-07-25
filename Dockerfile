FROM docker.elastic.co/beats/filebeat:8.13.4

COPY filebeat.yml /usr/share/filebeat/filebeat.yml

USER root
RUN chmod 644 /usr/share/filebeat/filebeat.yml && chown root:root /usr/share/filebeat/filebeat.yml
