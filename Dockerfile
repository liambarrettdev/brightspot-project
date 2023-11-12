FROM brightspot/mysql:mysql5.6
USER root
RUN sed -i 's/^\( *\)\(sleep 3\)/\1sleep 5/g' /entrypoint.sh
USER mysql