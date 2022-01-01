import {apiUrl} from "./environment";

export class UserDataController {
  TOKEN_KEY: string = "jwtToken";

  isLoggedIn() {
    const token = this.getJwtToken();
    let result = false;
    if (token != null) {
      const payload = this.parseJwt(token);
      if (payload.exp === 'undefined') {
        // Token is always valid so return true.
        result = true;
      } else {
        // Determine if the key is valid.
        let current_time = Date.now().valueOf() / 1000;
        if (payload.exp != null && payload.exp < current_time) {
          // Token is expired, remove from storage and return false.
          this.removeJwtToken();
        } else {
          result = true;
        }
      }
    }
    return result;
  }

  parseJwt(token) {
    let base64Url = token.split('.')[1];
    let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    let jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
  };

  // For now: store tokens in local storage. In case we want to make it more secure, consider something like this:
  // https://medium.com/lightrail/getting-token-authentication-right-in-a-stateless-single-page-application-57d0c6474e3
  getJwtToken() {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  setJwtToken(token) {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  removeJwtToken() {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  login(username: string, password: string) {
    let payload = {
      "username": username,
      "password": password
    }
    return fetch(apiUrl('/login'), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }, body: JSON.stringify(payload)
    }).then(response => {
      if (!response.ok) {
        return Promise.reject(response.statusText);
      } else {
        return new Promise(resolve => resolve(response.json()));
      }
    }).then(body => {
      if (body['token']) {
        this.setJwtToken(body['token']);
        return new Promise(resolve => resolve(body['token']));
      } else {
        return Promise.reject('Could not obtain token from body after logging in.');
      }
    });
  }

  createAuthorizationTokenHeader() {
    const token = this.getJwtToken();
    if (token) {
      return {"Authorization": "Bearer " + token};
    } else {
      return {};
    }
  }

  logout() {
    this.removeJwtToken();
    window.dispatchEvent(new Event('logout'));
  }
}

export const UserData = new UserDataController();
