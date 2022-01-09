import {Component, h} from '@stencil/core';

@Component({
  tag: 'trader-spinner',
  styleUrl: 'trader-spinner.css'
})
export class TraderSpinner {
  render() {
    return (
      <div id="trader-spinner" class="row center">
        <div class="lds-grid">
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
        </div>
      </div>
    );
  }
}
