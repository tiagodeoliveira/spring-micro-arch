FROM java:8
ADD build/distributions/receiver.tar /
RUN chmod a+x /receiver/bin/receiver
EXPOSE 40080
CMD /receiver/bin/receiver