import {UserData} from "./user-data";

function createRelativeUrl(protocol, path, port?) {

  if (isLocalHost()) {
    return trimSlashes(`${protocol}//localhost:${port || '8080'}/${path}`);
  }

  let host = location.host;
  if (port) {
    host = host + ':' + port;
  }

  const urlParts = [
    host,
    trimSlashes(path)
  ]
    .map(part => part || '')
    .filter(part => part.trim().length > 0);
  return `${protocol}//${urlParts.join('/')}`;
}

function isLocalHost() {
  return location.host.includes('localhost') || location.host.includes('127.0.0.1');
}

/**
 * Removes slashes before and after the given string. Ie. /test/a/b/c/ becomes: test/a/b/c.
 *
 * @param string
 */
function trimSlashes(string) {
  if (string && string.trim().length > 0) {
    return string.replace(/^\/|\/$/g, '');
  } else {
    return '';
  }
}

/**
 * Export functions.
 */
export function apiUrl(path) {
  return `${createRelativeUrl(location.protocol, 'api')}${path || ''}`;
}

export function credentials() {
  const host = location.host;

  if (host.includes('localhost') || host.includes('127.0.0.1')) {
    return 'same-origin';
  }

  return 'include';
}

export function postToProtectedApi(url, body) {
  return fetch(apiUrl(url), {
    method: 'POST',
    headers: createJsonAuthorizationHeaders(),
    body: body
  });
}

export function getFromProtectedApi(url) {
  return fetch(apiUrl(url), {
    method: 'GET',
    headers: createJsonAuthorizationHeaders()
  });
}

export function putToProtectedApi(url, body) {
  return fetch(apiUrl(url), {
    method: 'PUT',
    headers: createJsonAuthorizationHeaders(),
    body: body
  });
}

export function deleteFromProtectedApi(url) {
  return fetch(apiUrl(url), {
    method: 'DELETE',
    headers: createJsonAuthorizationHeaders()
  });
}

function createJsonAuthorizationHeaders() {
  let headers = {
    'Content-Type': 'application/json'
  };
  return {...headers, ...UserData.createAuthorizationTokenHeader()};
}
