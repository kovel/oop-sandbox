/**
 * Copyright 2017 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

const fs = require('fs');
const url = require('url');
const http = require('http');
const puppeteer = require('puppeteer');

const server = http.createServer(async (req, res) => {// url.parse(req.url)
  (async () => {
    const browser = await puppeteer.launch({
      headless: 'new',
      executablePath: '/app/chrome/linux-117.0.5929.0/chrome-linux64/chrome'
    });
    const page = await browser.newPage();
    await page.goto(url.parse(req.url).query, {
      waitUntil: 'networkidle2',
    });
    // page.pdf() is currently supported only in headless mode.
    // @see https://bugs.chromium.org/p/chromium/issues/detail?id=753118
    await page.pdf({
      path: 'hn.pdf',
      format: 'letter',
    });

    await browser.close();
  })();

  const buffer = fs.readFileSync('hn.pdf')
  res.writeHead(200, { 'Content-Type': 'application/pdf' });
  res.end(buffer)
});
server.listen(8080)