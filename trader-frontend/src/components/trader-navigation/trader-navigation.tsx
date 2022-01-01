import {Component, Element, h} from '@stencil/core';
import M from 'materialize-css';

@Component({
  tag: 'trader-navigation'
})
export class TraderNavigation {
  @Element() element: HTMLElement;

  componentDidLoad() {
    let nav = this.element.querySelector('.sidenav');

    M.Sidenav
      .init(nav, {
        closeOnClick: true
      });
  }

  render() {
    return (
      <div>
        <div class="navbar-fixed">
          <nav class="navbar white">
            <div class="nav-wrapper">
              <a href="#" data-target="smaller_menu" class="sidenav-trigger left">
                <i class="material-icons black-text">menu</i>
              </a>
              <a href="#" class="brand-logo grey-text text-darken-4"></a>
            </div>
          </nav>
        </div>
        <ul class="sidenav" id="smaller_menu">
          <li>
            <a class="sidenav-close" href="#/dashboard">
              <i class="material-icons">dashboard</i>
              <span>Dashboard</span>
            </a>
          </li>
          <li>
            <a class="sidenav-close" href="#/settings">
              <i class="material-icons">settings</i>
              <span>Settings</span>
            </a>
          </li>
          <li>
            <a class="sidenav-close" href="#/orders">
              <i class="material-icons">build</i>
              <span>Orders</span>
            </a>
          </li>
        </ul>

        <ul class="sidenav sidenav-fixed" id="sidenav_left">
          <li>
            <trader-user-view/>
          </li>
          <li>
            <a href="#/dashboard">
              <i class="material-icons">dashboard</i>
              <span>Dashboard</span>
            </a>
          </li>
          <li>
            <a href="#/settings">
              <i class="material-icons">settings</i>
              <span>Settings</span>
            </a>
          </li>
          <li>
            <a href="#/orders">
              <i class="material-icons">build</i>
              <span>Orders</span>
            </a>
          </li>
          <li>
            <div class="divider"/>
          </li>
        </ul>
      </div>
    );
  }
}
