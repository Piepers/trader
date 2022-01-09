import {Component, h, State} from '@stencil/core';
import {TraderAccountService} from './trader-account-service';
import {changeBarTitle, changePageTitle} from '../../global/ui';

@Component({
  tag: 'trader-account'
})

export class TraderAccount {
  traderAccountService: TraderAccountService;
  @State() accounts = [];
  @State() accountCount = 0;

  componentWillLoad() {
    this.traderAccountService = new TraderAccountService();
    this.traderAccountService
      .findAll()
      .then(accountBag => {
      this.accounts = accountBag['accounts'];
      this.accountCount = accountBag['count'];
    })
  }

  componentDidLoad() {
    changeBarTitle('Account Overview');
    changePageTitle('Trader | Accounts');
  }

  render() {
    return (
      <div>
            <trader-account-list class="trader-account__list" accounts={this.accounts} accountCount={this.accountCount}/>
      </div>
    );
  }
}
