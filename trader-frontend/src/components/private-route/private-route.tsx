import {h} from '@stencil/core';
import {UserData} from "../../global/user-data";

export const PrivateRoute = ({ component, ...props}: {[key: string]: any}) => {
  const Component = component;
  // const redirectUrl = props.failureRedirect | '/login';


  return (
    <stencil-route {...props} routeRender={
      (props: { [key: string]: any }) => {
        const isAuthenticated = UserData.isLoggedIn();
        if (isAuthenticated) {
          return <Component {...props} {...props.componentProps}></Component>;
        }
        return <stencil-router-redirect url="/login"></stencil-router-redirect>
      }
    }/>
  );
}
