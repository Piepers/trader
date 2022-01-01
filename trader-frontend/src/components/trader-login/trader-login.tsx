import {Component, Event, EventEmitter, h, State} from "@stencil/core";
import {UserData} from "../../global/user-data";
import {showErrorToast} from "../../global/ui";

@Component({
    tag: 'trader-login'
  }
)
export class TraderLogin {
  @State() username = {
    valid: false,
    value: ''
  };

  @State() password = {
    valid: false,
    value: ''
  };

  @State() submitted = false;
  @Event() userLoggedIn: EventEmitter;

  render() {
    return (
      <div class="row">
        <div class="col s6 offset-s2">
          <form novalidate="true" onSubmit={(e) => this.onLogin(e)}>
            <div class="card large">
              <div class="card-image">
                <img src="../../assets/images/trader.jpg"></img>
                <span class="card-title">Log in with your account</span>
              </div>
              <div class="card-content">
                <div class="input-field">
                  <input id="username" class="username" type="text" value={this.username.value}
                         onInput={(event: any) => this.handleUsername(event.target.value)}
                         required/>
                  <label class="active" htmlFor="username">Username</label>
                </div>
                <div class="input-field">
                  <input id="password" class="password" type="password" value={this.password.value}
                         onInput={(event: any) => this.handlePassword(event.target.value)}
                         required/>
                  <label class="active" htmlFor="password">Password</label>
                </div>
              </div>
              <div class="card-action">
                <button id="login-btn" type="submit" class="btn right green">Login</button>
              </div>
            </div>
          </form>
        </div>
      </div>
    );
  }

  private handleUsername(value: any) {
    this.username.value = value;
  }

  private handlePassword(value: any) {
    this.password.value = value;
  }

  onLogin(e: any) {
    e.preventDefault();
    document.getElementById('login-btn').setAttribute('disabled', 'true');
    UserData
      .login(this.username.value, this.password.value)
      .then(result => {
        document.getElementById('login-btn').removeAttribute('disabled');
        this.userLoggedIn.emit(result);})
      // .catch((err) => {
      .catch(() => {
        showErrorToast('Login failed: invalid username or password.');
        document.getElementById('login-btn').removeAttribute('disabled');
      });

  }

}

