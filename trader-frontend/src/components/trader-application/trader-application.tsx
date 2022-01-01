import {Component, h, Listen, State} from "@stencil/core";
import {UserData} from "../../global/user-data";
import {PrivateRoute} from "../private-route/private-route";

@Component({
  tag: 'trader-application',
  styleUrl: 'trader-application.css'
})
export class TraderApplication {

  @State()
  loggedIn: boolean = false;

  render() {
    if (UserData.isLoggedIn()) {
      return (
        <div>
          {this.renderHeader()}
          <main>
            {this.renderRouter()}
          </main>
        </div>
      )
    } else {
      return (
        <div class="container">
          {this.renderLogin()}
        </div>
      )
    }
  }

  renderHeader() {
    return (
      <trader-header></trader-header>
    );
  }

  renderLogin() {
    return (
      <trader-login onUserLoggedIn={() => this.userLoggedIn()}></trader-login>
    );
  }

  renderRouter() {
    return (
      <stencil-router historyType="hash">
        <stencil-route-switch scrollTopOffset={0}>
          <PrivateRoute url={["/", "/dashboard"]} component="trader-dashboard" exact/>
          <PrivateRoute url="/orders" component="trader-orders"/>
          <PrivateRoute url="/settings" component="trader-settings"/>
        </stencil-route-switch>
      </stencil-router>
    );
  }

  private userLoggedIn() {
    this.loggedIn = true;
  }

  @Listen('logout', {target: 'window'})
  userLoggedOut() {
    this.loggedIn = false;
  }
}
