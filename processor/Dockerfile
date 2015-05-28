FROM java:8
ADD build/distributions/processor.tar /
RUN chmod a+x /processor/bin/processor
EXPOSE 40083
CMD /processor/bin/processor