{ pkgs ? import <nixpkgs> {} }:
  let
    pinned = import (builtins.fetchGit {
               name = "nixos-unstable-2020-12-01";
               url = https://github.com/nixos/nixpkgs-channels/;
               # `git ls-remote https://github.com/nixos/nixpkgs-channels nixos-unstable`
               ref = "refs/heads/nixos-unstable";
               rev = "7184df6beb88c4f5f3186e5b73d5437a3461ceaf";
    }) {};
    pkgs = import <nixpkgs> { overlays = [ (self: super: {
      jdk = super.openjdk8;
      maven = pinned.maven3; 
    }) ]; };
  in
  with pkgs;
  mkShell {
    buildInputs = [ jdk
                    maven
                    elmPackages.elm
                    elmPackages.elm-live
                    elmPackages.elm-xref
                    elmPackages.elm-analyse
                    elmPackages.elm-test
                    elmPackages.elm-doc-preview
                    elmPackages.elm-upgrade
                    elmPackages.elmi-to-json
                    elmPackages.elm-format
                    elmPackages.elm-verify-examples
                    nodePackages.node2nix
                  ];
  }
