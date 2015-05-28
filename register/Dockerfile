FROM java:8
ADD build/distributions/register.tar /
RUN chmod a+x /register/bin/register
EXPOSE 40082
CMD /register/bin/register