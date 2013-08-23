This resolver subclasses the StandardOutputResolver from Saxon9 to be able to use the pound sign (#) in result-document uris.
The method 'resolve' overrides the method 'resolve' in the StandardOutputResolver class.

When the resulting URI has a 'file' scheme and contains a pound sign, this resolver creates the the correct result-document filenames.

It is used in the betty-poc project to be able to generate the outputfiles for Hulk