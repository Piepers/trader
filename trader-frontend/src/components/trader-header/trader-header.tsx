import {Component, h} from '@stencil/core';

@Component({
  tag: 'trader-header'
})
export class TraderHeader {
  render() {
    return (
        <header>
          <trader-navigation/>
        </header>
    );
  }
}
