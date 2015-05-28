FROM java:8
ADD build/distributions/provider.tar /
RUN chmod a+x /provider/bin/provider
EXPOSE 40084
CMD /provider/bin/provider