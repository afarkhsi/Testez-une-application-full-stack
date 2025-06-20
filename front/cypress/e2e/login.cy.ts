/// <reference types="Cypress" />

/// <reference types="Cypress" />

describe('Login Page', () => {
  // Avant chaque test on se retrouve sur la page de login
  beforeEach(() => {
    cy.visit('/login');
  });

  it('should login successfully and redirect to sessions page', () => {

    // Intercepte l'appel de login et mock la réponse
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        email: 'yoga@studio.com',
        firstName: 'Admin',
        lastName: 'Admin',
        admin: true,
      },
    }).as('loginRequest')

    // Intercepte la récupération des sessions (si ton app le fait au chargement)
    cy.intercept('GET', '/api/session', []).as('getSessions')

    // Remplis les champs de formulaire
    cy.get('input[formControlName=email]').type('yoga@studio.com')
    cy.get('input[formControlName=password]').type('test!1234')

    // Clique sur le bouton submit
    cy.get('button[type=submit]').click()

    // Attend que l’appel API de login soit fait
    cy.wait('@loginRequest')

    // Vérifie qu'on est redirigé vers la page des sessions
    cy.url().should('include', '/sessions')
  })

   it('should logout and redirect to home page', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        email: 'yoga@studio.com',
        firstName: 'Admin',
        lastName: 'Admin',
        admin: true,
        token: 'fake-jwt-token'
      },
    }).as('loginRequest')

    cy.intercept('GET', '/api/session', []).as('getSessions')

    cy.get('input[formControlName=email]').type('yoga@studio.com')
    cy.get('input[formControlName=password]').type('test!1234')
    cy.get('button[type=submit]').click()

    cy.wait('@loginRequest')
    cy.url().should('include', '/sessions')

    cy.get('.link').contains('Logout').click();
    // Vérifie que l'utilisateur est redirigé vers la page home
    cy.url().should('include', '/');
  })

  it('should return error if one of the inputs is not valid', () => {
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!12345'); // Mot de passe incorrect
    cy.get('button[type=submit]').click();
    cy.get('.error').should('be.visible');
  });
})
