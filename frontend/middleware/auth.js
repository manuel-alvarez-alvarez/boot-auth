export default function (ctx) {
  if(!isAuth()) {
    return ctx.redirect('/login')
  }
}

function isAuth() {
  // Check if user session exists somehow
  return false;
}
