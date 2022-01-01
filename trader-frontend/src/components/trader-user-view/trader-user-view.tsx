import {Component, h, State} from '@stencil/core';
import {getFromProtectedApi} from '../../global/environment';
import {UserData} from '../../global/user-data';

@Component({
  tag: 'trader-user-view',
  styleUrl: 'trader-user-view.css'
})
export class TraderUserView {
  @State() name: string;
  @State() imgSrc: string;
  @State() imgStyle: string;

  ANONYMOUS_USER: string = 'Anonymous user';

  componentDidLoad() {
    getFromProtectedApi('/admin/user/info')
      .then(response => response.json())
      .then(data => this.parseData(data))
      .catch(() => this.parseData({
        name: this.ANONYMOUS_USER,
        imgSrc: "../../assets/images/user.png"
      }));
  }

  parseData(data) {
    this.name = this.ANONYMOUS_USER;
    this.imgSrc = "../../assets/images/user.png";
    if (data.username) {
      this.name = data.username;
    }

    if (data.thumb) {
      this.imgSrc = data.thumb;
      this.imgStyle = '{backgound-image: ' + this.imgSrc + ';}'
    }
  }

  render() {
    return (
      <div class="user-view">
        <div class="background">
          <img src="../../assets/images/user-bg.jpg"/>
        </div>
        <img class="userimg" src={this.imgSrc}/>
        <span class="white-text name">{this.name}</span>
        <a href="#" onClick={() => this.onLogout()}><span class="white-text clogout">logout</span></a>
      </div>
    )
  }

  private onLogout() {
    UserData.logout();
  }
}
