FROM node:18

WORKDIR /app
RUN npx @puppeteer/browsers install chrome
RUN apt-get update && \
    apt-get -y install libnss3-dev libatk1.0-dev libatk-bridge2.0-dev libcups2-dev libdrm-dev libxkbcommon-dev \
                        libxcomposite-dev libxdamage-dev libxrandr-dev libgbm-dev libasound2-dev

COPY ./package.json /app/
RUN npm i

ENV PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=false
COPY ./pdf.js /app/
COPY ./sandbox.proto /app/

CMD ["node", "pdf.js"]