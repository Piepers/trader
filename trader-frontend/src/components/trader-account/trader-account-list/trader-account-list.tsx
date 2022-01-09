import {Component, Element, h, Prop} from '@stencil/core';

@Component({
  tag: 'trader-account-list'
})
export class TraderAccountList {
  @Prop() accounts;
  @Prop() accountCount;

  @Element() element: HTMLElement;

  componentDidRender() {
    // let spinner = this.element.querySelector('.trader-spinner');
    // if (!spinner.classList.contains('hide')) {
    //   spinner.classList.add('hide');
    // }
    // let elAccountList = this.element.querySelector('.trader-account-list');
    // let elEmptyContent = this.element.querySelector('.trader-account-empty');
    // toggleContent(elAccountList, elEmptyContent, this.accountCount);
  }

  render() {
    return (
      <div>
        {this.accounts.map(account => {
          return (
            <div class="row">
              <trader-account-item account={account}/>
            </div>
          );
        })}
      </div>
    );
  }
}
