import {Component, h} from '@stencil/core';
import {changeBarTitle, changePageTitle} from '../../global/ui';

@Component({
  tag: 'trader-dashboard',
})
export class TraderDashboard {

  componentWillLoad() {
  }

  componentDidLoad() {
    changeBarTitle('Dashboard');
    changePageTitle('Trader | Dashboard');
  }

  render() {
    return (
      <div>
        <div class="row">
          <trader-account/>
        </div>
      </div>
    );
  }
}
