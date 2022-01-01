/* eslint-disable */
/* tslint:disable */
/**
 * This is an autogenerated file created by the Stencil compiler.
 * It contains typing information for all components that exist in this project.
 */
import { HTMLStencilElement, JSXBase } from "@stencil/core/internal";
import { MatchResults } from "@stencil/router";
export namespace Components {
    interface AppHome {
    }
    interface AppProfile {
        "match": MatchResults;
    }
    interface AppRoot {
    }
    interface TraderApplication {
    }
    interface TraderDashboard {
    }
    interface TraderLogin {
    }
    interface TraderNavigation {
    }
    interface TraderUserView {
    }
}
declare global {
    interface HTMLAppHomeElement extends Components.AppHome, HTMLStencilElement {
    }
    var HTMLAppHomeElement: {
        prototype: HTMLAppHomeElement;
        new (): HTMLAppHomeElement;
    };
    interface HTMLAppProfileElement extends Components.AppProfile, HTMLStencilElement {
    }
    var HTMLAppProfileElement: {
        prototype: HTMLAppProfileElement;
        new (): HTMLAppProfileElement;
    };
    interface HTMLAppRootElement extends Components.AppRoot, HTMLStencilElement {
    }
    var HTMLAppRootElement: {
        prototype: HTMLAppRootElement;
        new (): HTMLAppRootElement;
    };
    interface HTMLTraderApplicationElement extends Components.TraderApplication, HTMLStencilElement {
    }
    var HTMLTraderApplicationElement: {
        prototype: HTMLTraderApplicationElement;
        new (): HTMLTraderApplicationElement;
    };
    interface HTMLTraderDashboardElement extends Components.TraderDashboard, HTMLStencilElement {
    }
    var HTMLTraderDashboardElement: {
        prototype: HTMLTraderDashboardElement;
        new (): HTMLTraderDashboardElement;
    };
    interface HTMLTraderLoginElement extends Components.TraderLogin, HTMLStencilElement {
    }
    var HTMLTraderLoginElement: {
        prototype: HTMLTraderLoginElement;
        new (): HTMLTraderLoginElement;
    };
    interface HTMLTraderNavigationElement extends Components.TraderNavigation, HTMLStencilElement {
    }
    var HTMLTraderNavigationElement: {
        prototype: HTMLTraderNavigationElement;
        new (): HTMLTraderNavigationElement;
    };
    interface HTMLTraderUserViewElement extends Components.TraderUserView, HTMLStencilElement {
    }
    var HTMLTraderUserViewElement: {
        prototype: HTMLTraderUserViewElement;
        new (): HTMLTraderUserViewElement;
    };
    interface HTMLElementTagNameMap {
        "app-home": HTMLAppHomeElement;
        "app-profile": HTMLAppProfileElement;
        "app-root": HTMLAppRootElement;
        "trader-application": HTMLTraderApplicationElement;
        "trader-dashboard": HTMLTraderDashboardElement;
        "trader-login": HTMLTraderLoginElement;
        "trader-navigation": HTMLTraderNavigationElement;
        "trader-user-view": HTMLTraderUserViewElement;
    }
}
declare namespace LocalJSX {
    interface AppHome {
    }
    interface AppProfile {
        "match"?: MatchResults;
    }
    interface AppRoot {
    }
    interface TraderApplication {
    }
    interface TraderDashboard {
    }
    interface TraderLogin {
        "onUserLoggedIn"?: (event: CustomEvent<any>) => void;
    }
    interface TraderNavigation {
    }
    interface TraderUserView {
    }
    interface IntrinsicElements {
        "app-home": AppHome;
        "app-profile": AppProfile;
        "app-root": AppRoot;
        "trader-application": TraderApplication;
        "trader-dashboard": TraderDashboard;
        "trader-login": TraderLogin;
        "trader-navigation": TraderNavigation;
        "trader-user-view": TraderUserView;
    }
}
export { LocalJSX as JSX };
declare module "@stencil/core" {
    export namespace JSX {
        interface IntrinsicElements {
            "app-home": LocalJSX.AppHome & JSXBase.HTMLAttributes<HTMLAppHomeElement>;
            "app-profile": LocalJSX.AppProfile & JSXBase.HTMLAttributes<HTMLAppProfileElement>;
            "app-root": LocalJSX.AppRoot & JSXBase.HTMLAttributes<HTMLAppRootElement>;
            "trader-application": LocalJSX.TraderApplication & JSXBase.HTMLAttributes<HTMLTraderApplicationElement>;
            "trader-dashboard": LocalJSX.TraderDashboard & JSXBase.HTMLAttributes<HTMLTraderDashboardElement>;
            "trader-login": LocalJSX.TraderLogin & JSXBase.HTMLAttributes<HTMLTraderLoginElement>;
            "trader-navigation": LocalJSX.TraderNavigation & JSXBase.HTMLAttributes<HTMLTraderNavigationElement>;
            "trader-user-view": LocalJSX.TraderUserView & JSXBase.HTMLAttributes<HTMLTraderUserViewElement>;
        }
    }
}
