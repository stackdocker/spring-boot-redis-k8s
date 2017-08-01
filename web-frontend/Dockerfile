FROM nginx
COPY eventer/nginx.conf /etc/nginx/nginx.conf
COPY eventer/ /opt/src/eventer
RUN apt-get update
RUN apt-get install -y curl gnupg2
RUN curl -sL https://deb.nodesource.com/setup_6.x | bash
RUN apt-get install -y nodejs
RUN npm install -g @angular/cli@1.2.6
WORKDIR /opt/src/eventer
RUN ng build
RUN cp -r dist/* /usr/share/nginx/html
