const { runProcess } = require('./lib/generator');

runProcess().catch(err => {
  console.error(err);
  process.exit(1);
});
