const fs = require('fs');
const path = require('path');

const loadJson = (src) => {
    const file = path.resolve(src);
    let rawdata = fs.readFileSync(file);
    return JSON.parse(rawdata);
}

const writeJson = (src, content) => {
    const file = path.resolve(src);
    let data = JSON.stringify(content);
    fs.writeFileSync(file, data);
}

module.exports = {
    loadJson,
    writeJson
}