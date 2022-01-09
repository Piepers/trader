import {Component, h, Prop} from '@stencil/core';

@Component({
  tag: 'trader-account-item'
})
export class TraderAccountItem {
  @Prop() account: any = {};

  render() {
    return (
      <div class="col s12 m6">
        <div class="card">
          <div class="card-content">
            <span class="card-title">{this.account.exchange.name}</span>
            <table class="highlight sortable-theme-minimal" data-sortable>
              <thead>
              <tr>
                <th>Asset</th>
                <th>Free</th>
                <th>Locked</th>
              </tr>
              </thead>
              <tbody>
              {this.account.assetBalances.map(asset => {
                return (
                  <tr>
                    <td>{asset.asset}</td>
                    <td>{asset.free}</td>
                    <td>{asset.locked}</td>
                  </tr>
                );
              })
              }
              </tbody>
            </table>
          </div>
        </div>
      </div>
    );
  }
}
