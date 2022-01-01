import M from 'materialize-css';

/**
 * Change dashboard title bar.
 */
export function changeBarTitle(title) {
  const brandLogo = document.querySelector('.brand-logo');
  const titleNode = document.createTextNode(title);
  appendOrReplace(titleNode, brandLogo);
}

/**
 * Append or replace element.
 *
 * @param element
 * @param brandLogo
 */
function appendOrReplace(element, brandLogo) {
  if (brandLogo.hasChildNodes()) {
    brandLogo.replaceChild(element, brandLogo.firstChild);
  } else {
    brandLogo.appendChild(element);
  }
}

/**
 * Change dashboard title bar.
 */
export function changePageTitle(title) {
  const brandLogo = document.querySelector('head title');
  const titleNode = document.createTextNode(title);

  appendOrReplace(titleNode, brandLogo);
}

/**
 * Remove all the children from the element.
 *
 * @param element targeted
 */
export function removeChildren(element) {
  while (element.hasChildNodes()) {
    element.removeChild(element.lastChild);
  }
}

/**
 * It will infer what component is hidden.
 *
 * @param component the original
 * @param emptyComponent the empty content indicator
 * @param count number of items to check the toggling
 */
export function toggleContent(componentToShow, componentToHide, count) {
  if (componentToShow && componentToShow.classList) {
    if (count && count > 0) {
      componentToShow.classList.remove('hide');
      componentToHide.classList.add('hide');
    } else {
      componentToShow.classList.add('hide');
      componentToHide.classList.remove('hide');
    }
  } else {
    console.error("Could not find " + componentToShow);
  }
}

/**
 * Show a toast stylized for error.
 */
export function showErrorToast(message) {
  M.toast({html: message, classes: 'red'})
}

/**
 * Show a toast stylized for error.
 */
export function showSuccessToast(message) {
  M.toast({html: message, classes: 'green'})
}

/**
 * A generic sort function for table content.
 */
export function getSortOrder(field) {
  return function (a, b) {
    if (a[field] > b[field]) {
      return 1;
    } else if (a[field] < b[field]) {
      return -1;
    }
    return 0;
  }
}

