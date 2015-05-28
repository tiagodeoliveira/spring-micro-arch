FROM java:8
ADD build/distributions/sender.tar /
RUN chmod a+x /sender/bin/sender
EXPOSE 40081
CMD /sender/bin/sender