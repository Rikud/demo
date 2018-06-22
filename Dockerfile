FROM ubuntu:16.04

MAINTAINER Ivan Nemshilov

# Обвновление списка пакетов
RUN apt-get -y update

#
# Установка postgresql
#
ENV PGVER 9.5
RUN apt-get install -y postgresql-$PGVER

# Run the rest of the commands as the ``postgres`` user created by the ``postgres-$PGVER`` package when it was ``apt-get installed``
USER postgres

# Create a PostgreSQL role named ``docker`` with ``docker`` as the password and
# then create a database `docker` owned by the ``docker`` role.
RUN /etc/init.d/postgresql start &&\
    psql --command "CREATE USER docker WITH SUPERUSER PASSWORD 'docker';" &&\
    createdb -O docker docker &&\
    /etc/init.d/postgresql stop

# Adjust PostgreSQL configuration so that remote connections to the
# database are possible.
RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/$PGVER/main/pg_hba.conf

# And add ``listen_addresses`` to ``/etc/postgresql/$PGVER/main/postgresql.conf``
RUN echo "listen_addresses='*'" >> /etc/postgresql/9.5/main/postgresql.conf
RUN echo "synchronous_commit = off" >> /etc/postgresql/9.5/main/postgresql.conf
RUN echo "fsync = off" >> /etc/postgresql/9.5/main/postgresql.conf
RUN echo "random_page_cost = 1.0" >> /etc/postgresql/9.5/main/postgresql.conf

RUN echo "shared_buffers = 512MB" >> /etc/postgresql/9.5/main/postgresql.conf
RUN echo "work_mem = 8MB" >> /etc/postgresql/9.5/main/postgresql.conf
RUN echo "maintenance_work_mem = 128MB" >> /etc/postgresql/9.5/main/postgresql.conf
RUN echo "wal_buffers = 1MB" >> /etc/postgresql/9.5/main/postgresql.conf
RUN echo "effective_cache_size = 1024MB" >> /etc/postgresql/9.5/main/postgresql.conf
RUN echo "cpu_tuple_cost = 0.0030" >> /etc/postgresql/9.5/main/postgresql.conf
RUN echo "cpu_index_tuple_cost = 0.0010" >> /etc/postgresql/9.5/main/postgresql.conf
RUN echo "cpu_operator_cost = 0.0005" >> /etc/postgresql/9.5/main/postgresql.conf

# Expose the PostgreSQL port
EXPOSE 5432

# Add VOLUMEs to allow backup of config, logs and databases
VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

# Back to the root user
USER root

#
# Сборка проекта
#

# Установка JDK
RUN apt-get install -y openjdk-8-jdk-headless

# Копируем исходный код в Docker-контейнер
ADD / /demo/

# Собираем и устанавливаем пакет
WORKDIR /demo
RUN ./gradlew assemble

# Объявлем порт сервера
EXPOSE 5000

#
# Запускаем PostgreSQL и сервер
#
CMD service postgresql start && java -jar /demo/build/libs/demo.jar --database=jdbc:postgresql://localhost/docker --username=docker --password=docker
