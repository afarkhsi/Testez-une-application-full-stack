/// <reference types="Cypress" />
describe('404 not found', () => {
  it('should display the 404 notfound page if the url does not exist', () => {
    cy.visit('/doesntexist');

    cy.get('h1').should('contain', 'Page not found !');
  });
});