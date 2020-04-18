const remoteHost = "http://localhost:3030";

export async function get(url) {
    try {
        const res = await fetch(remoteHost.concat(url));
        const json = await res.json();
        console.log("remote response", json)
        return json;
    } catch (err) {
        return ({ data: {}, error: err })
    }
}

export async function del(url) {
    try {
        const res = await fetch(remoteHost.concat(url), {
            method: 'delete',
            headers: new Headers({
                'Accept': 'application/json, application/xml, text/plain, text/html, *.*'
            }),
        });
        const json = await res.json();
        console.log("remote response", json)
        return json;
    } catch (err) {
        return ({ data: {}, error: err })
    }
}

export async function post(url, data) {
    try {
        const res = await fetch(remoteHost.concat(url), {
            method: 'post',
            body: JSON.stringify(data),
            headers: new Headers({
                'content-type': 'application/json; charset=utf-8',
                'Accept': 'application/json, application/xml, text/plain, text/html, *.*'
            }),
        });
        const json = await res.json();
        console.log("remote response", json)
        return json;
    } catch (err) {
        return ({ data: {}, error: err })
    }
}

export async function put(url, data) {
    try {
        const res = await fetch(remoteHost.concat(url), {
            method: 'put',
            body: JSON.stringify(data),
            headers: new Headers({
                'content-type': 'application/json; charset=utf-8',
                'Accept': 'application/json, application/xml, text/plain, text/html, *.*'
            }),
        });
        const json = await res.json();
        console.log("remote response", json)
        return json;
    } catch (err) {
        return ({ data: {}, error: err })
    }
}