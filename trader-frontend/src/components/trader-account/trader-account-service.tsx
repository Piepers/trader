import {getFromProtectedApi} from '../../global/environment';

export class TraderAccountService {
  findAll() {
    return getFromProtectedApi('/accounts')
      .then(response => new Promise(resolve => resolve(response.json())));
  }
}
