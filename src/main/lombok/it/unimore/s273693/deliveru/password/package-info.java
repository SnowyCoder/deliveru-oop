/**
 * This package provides an abstraction layer to password authentication.
 *
 * <br>
 * The package is divided between the manager, {@link it.unimore.s273693.deliveru.password.PasswordAuthenticator} and
 * its manager (that need to implement {@link it.unimore.s273693.deliveru.password.PasswordStorageStrategy}.
 * The only password hashing strategies implemented are:
 * <ul>
 *     <li>{@link it.unimore.s273693.deliveru.password.PlainPasswordStorageStrategy}</li>
 *     <li>{@link it.unimore.s273693.deliveru.password.Sha256PasswordStorageStrategy}</li>
 *     <li>{@link it.unimore.s273693.deliveru.password.PBKDF2PasswordStorageStrategy}</li>
 * </ul>
 * <br>
 * <br>
 * Another class present in the package and used by the strategy implementations is
 * {@link it.unimore.s273693.deliveru.password.PasswordStorageUtil} but it's nothing more than some utility functions
 * that the storages have in common.
 */
package it.unimore.s273693.deliveru.password;